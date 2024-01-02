/*
 * Copyright © 2023 Mark Raynsford <code@io7m.com> https://www.io7m.com
 *
 * Permission to use, copy, modify, and/or distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY
 * SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR
 * IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */


package com.io7m.cardant.type_packages.upgrades;

import com.io7m.cardant.error_codes.CAStandardErrorCodes;
import com.io7m.cardant.model.CATypeRecord;
import com.io7m.cardant.model.CATypeRecordFieldUpdate;
import com.io7m.cardant.model.CATypeRecordRemoval;
import com.io7m.cardant.model.CATypeScalarRemoval;
import com.io7m.cardant.model.type_package.CATypePackage;
import com.io7m.cardant.model.type_package.CATypePackageUpgrade;
import com.io7m.cardant.strings.CAStringConstantType;
import com.io7m.cardant.strings.CAStrings;
import com.io7m.cardant.type_packages.resolver.api.CATypePackageResolverType;
import com.io7m.cardant.type_packages.upgrades.api.CATypePackageUOpFail;
import com.io7m.cardant.type_packages.upgrades.api.CATypePackageUOpPackageInstall;
import com.io7m.cardant.type_packages.upgrades.api.CATypePackageUOpPackageSetVersion;
import com.io7m.cardant.type_packages.upgrades.api.CATypePackageUOpType;
import com.io7m.cardant.type_packages.upgrades.api.CATypePackageUOpTypeRecordCreate;
import com.io7m.cardant.type_packages.upgrades.api.CATypePackageUOpTypeRecordFieldRemove;
import com.io7m.cardant.type_packages.upgrades.api.CATypePackageUOpTypeRecordFieldUpdate;
import com.io7m.cardant.type_packages.upgrades.api.CATypePackageUOpTypeRecordRemove;
import com.io7m.cardant.type_packages.upgrades.api.CATypePackageUOpTypeScalarRemove;
import com.io7m.cardant.type_packages.upgrades.api.CATypePackageUOpTypeScalarUpdate;
import com.io7m.cardant.type_packages.upgrades.api.CATypePackageUpgradePlannerType;
import com.io7m.jaffirm.core.Invariants;
import com.io7m.seltzer.api.SStructuredError;
import com.io7m.verona.core.Version;
import com.io7m.verona.core.VersionRange;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static com.io7m.cardant.strings.CAStringConstants.ERROR_PACKAGE_DOWNGRADE;
import static com.io7m.cardant.strings.CAStringConstants.ERROR_PACKAGE_DOWNGRADE_SUGGEST;
import static com.io7m.cardant.strings.CAStringConstants.FIELD_NAME;
import static com.io7m.cardant.strings.CAStringConstants.FIELD_TYPE;
import static com.io7m.cardant.strings.CAStringConstants.PACKAGE;
import static com.io7m.cardant.strings.CAStringConstants.PACKAGE_INSTALLED;
import static com.io7m.cardant.strings.CAStringConstants.PACKAGE_INSTALLED_VERSION;
import static com.io7m.cardant.strings.CAStringConstants.PACKAGE_VERSION;
import static com.io7m.cardant.strings.CAStringConstants.TYPE;

/**
 * A planner for type package upgrades.
 */

public final class CATypePackageUpgradePlanner
  implements CATypePackageUpgradePlannerType
{
  private final CAStrings strings;
  private final CATypePackageResolverType resolver;
  private final CATypePackageUpgrade upgrade;
  private final HashMap<String, String> attributes;
  private final LinkedList<CATypePackageUOpType> operations;

  private CATypePackageUpgradePlanner(
    final CAStrings inStrings,
    final CATypePackageResolverType inResolver,
    final CATypePackageUpgrade inUpgrade)
  {
    this.strings =
      Objects.requireNonNull(inStrings, "strings");
    this.resolver =
      Objects.requireNonNull(inResolver, "resolver");
    this.upgrade =
      Objects.requireNonNull(inUpgrade, "upgrade");
    this.attributes =
      new HashMap<>();
    this.operations =
      new LinkedList<>();
  }

  /**
   * A planner for type package upgrades.
   *
   * @param strings  The strings
   * @param upgrade  The upgrade
   * @param resolver The resolver
   *
   * @return A planner
   */

  public static CATypePackageUpgradePlannerType create(
    final CAStrings strings,
    final CATypePackageResolverType resolver,
    final CATypePackageUpgrade upgrade)
  {
    return new CATypePackageUpgradePlanner(strings, resolver, upgrade);
  }

  @Override
  public List<CATypePackageUOpType> plan()
  {
    this.attributes.clear();
    this.operations.clear();

    final var newTypePackage =
      this.upgrade.typePackage();
    final var identifier =
      newTypePackage.identifier();
    final var newVersion =
      identifier.version();

    this.setAttribute(PACKAGE, identifier.name());
    this.setAttribute(PACKAGE_VERSION, newVersion);

    final var existingPackageOpt =
      this.resolver.findTypePackageId(
        identifier.name(),
        new VersionRange(
          Version.of(0, 0, 0),
          true,
          Version.of(Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE),
          true
        )
      );

    /*
     * If no package is installed with the given name, then the upgrade is
     * simply an installation.
     */

    if (!existingPackageOpt.isPresent()) {
      this.operations.add(new CATypePackageUOpPackageInstall(newTypePackage));
      return this.finish();
    }

    final var existingPackageId = existingPackageOpt.get();
    this.setAttribute(PACKAGE_INSTALLED, existingPackageId.name());
    this.setAttribute(PACKAGE_INSTALLED_VERSION, existingPackageId.version());

    /*
     * If the installed package is newer than the old package, then we might
     * need to fail fast.
     */

    if (newVersion.compareTo(existingPackageId.version()) < 0) {
      switch (this.upgrade.versionBehavior()) {
        case VERSION_ALLOW_DOWNGRADES -> {
          // Not a problem.
        }
        case VERSION_DISALLOW_DOWNGRADES -> {
          this.operations.add(this.errorVersionDowngrade());
          return this.finish();
        }
      }
    }

    final var existingPackage =
      this.resolver.findTypePackage(existingPackageId)
        .orElseThrow();

    Invariants.checkInvariantV(
      Objects.equals(existingPackage.identifier(), existingPackageId),
      "Package identifiers must match"
    );

    this.planScalarChanges(existingPackage, newTypePackage);
    this.planRecordChanges(existingPackage, newTypePackage);

    this.operations.add(
      new CATypePackageUOpPackageSetVersion(newTypePackage.identifier())
    );
    return this.finish();
  }

  private List<CATypePackageUOpType> finish()
  {
    final var results = List.copyOf(this.operations);
    this.operations.clear();
    return results;
  }

  private void planScalarChanges(
    final CATypePackage packageExisting,
    final CATypePackage packageNew)
  {
    final var scalarsExisting =
      packageExisting.scalarTypes();
    final var scalarsNew =
      packageNew.scalarTypes();

    /*
     * Find scalar types that need to be removed.
     */

    final var entriesExist = new ArrayList<>(scalarsExisting.entrySet());
    entriesExist.sort(Map.Entry.comparingByKey());

    for (final var e : entriesExist) {
      final var typeExisting = e.getValue();
      this.setAttribute(TYPE, typeExisting.name());

      final var typeNew =
        scalarsNew.get(typeExisting.name());

      /*
       * If the type doesn't exist in the new package, then the type was
       * removed.
       */

      if (typeNew == null) {
        this.operations.add(
          new CATypePackageUOpTypeScalarRemove(
            new CATypeScalarRemoval(
              typeExisting,
              this.upgrade.typeRemovalBehavior()
            )
          )
        );
      }
    }

    /*
     * Find scalar types that need to be created/updated.
     */

    final var entriesNew = new ArrayList<>(scalarsNew.entrySet());
    entriesNew.sort(Map.Entry.comparingByKey());

    for (final var e : entriesNew) {
      final var typeNew = e.getValue();
      this.operations.add(new CATypePackageUOpTypeScalarUpdate(typeNew));
    }

    this.unsetAttribute(TYPE);
  }

  private void planRecordChanges(
    final CATypePackage packageExisting,
    final CATypePackage packageNew)
  {
    final var recordsExisting =
      packageExisting.recordTypes();
    final var recordsNew =
      packageNew.recordTypes();

    /*
     * Find record types that need to be removed.
     */

    final var entriesExist = new ArrayList<>(recordsExisting.entrySet());
    entriesExist.sort(Map.Entry.comparingByKey());

    for (final var e : entriesExist) {
      final var typeExisting = e.getValue();
      this.setAttribute(TYPE, typeExisting.name());

      final var typeNew =
        recordsNew.get(typeExisting.name());

      /*
       * If the type doesn't exist in the new package, then the type was
       * removed.
       */

      if (typeNew == null) {
        this.operations.add(
          new CATypePackageUOpTypeRecordRemove(
            new CATypeRecordRemoval(
              typeExisting,
              this.upgrade.typeRemovalBehavior()
            )
          )
        );
        continue;
      }
    }

    /*
     * Find record types that need to be created or updated.
     */

    final var entriesNew = new ArrayList<>(recordsNew.entrySet());
    entriesNew.sort(Map.Entry.comparingByKey());

    for (final var e : entriesNew) {
      final var typeNew = e.getValue();
      this.setAttribute(TYPE, typeNew.name());

      final var typeExisting =
        recordsExisting.get(typeNew.name());

      if (typeExisting != null) {
        this.planRecordChangesOne(typeExisting, typeNew);
      } else {
        this.operations.add(new CATypePackageUOpTypeRecordCreate(typeNew));
      }
    }

    this.unsetAttribute(TYPE);
  }

  private void planRecordChangesOne(
    final CATypeRecord typeExisting,
    final CATypeRecord typeNew)
  {
    final var fieldsNew =
      typeNew.fields();

    /*
     * Find record fields that need to be removed.
     */

    final var entriesExist = new ArrayList<>(typeExisting.fields().entrySet());
    entriesExist.sort(Map.Entry.comparingByKey());

    for (final var e : entriesExist) {
      final var fieldExisting = e.getValue();
      this.setAttribute(FIELD_NAME, fieldExisting.name());
      this.setAttribute(FIELD_TYPE, fieldExisting.type().name());

      final var fieldNew =
        fieldsNew.get(fieldExisting.name());

      /*
       * If the field doesn't exist in the new type, then the field was
       * removed.
       */

      if (fieldNew == null) {
        this.operations.add(
          new CATypePackageUOpTypeRecordFieldRemove(
            fieldExisting,
            this.upgrade.typeRemovalBehavior()
          )
        );
        continue;
      }
    }

    /*
     * Find record fields that need to be created/updated.
     */

    final var entriesNew = new ArrayList<>(typeNew.fields().entrySet());
    entriesNew.sort(Map.Entry.comparingByKey());

    for (final var e : entriesNew) {
      final var fieldNew = e.getValue();
      this.setAttribute(FIELD_NAME, fieldNew.name());
      this.setAttribute(FIELD_TYPE, fieldNew.type().name());
      this.operations.add(
        new CATypePackageUOpTypeRecordFieldUpdate(
          new CATypeRecordFieldUpdate(typeNew.name(), fieldNew)
        )
      );
    }

    this.unsetAttribute(FIELD_NAME);
    this.unsetAttribute(FIELD_TYPE);
  }

  private void unsetAttribute(
    final CAStringConstantType name)
  {
    this.attributes.remove(this.local(name));
  }

  private CATypePackageUOpFail errorVersionDowngrade()
  {
    return new CATypePackageUOpFail(
      new SStructuredError<>(
        CAStandardErrorCodes.errorOperationNotPermitted(),
        this.strings.format(ERROR_PACKAGE_DOWNGRADE),
        Map.copyOf(this.attributes),
        Optional.of(this.strings.format(ERROR_PACKAGE_DOWNGRADE_SUGGEST)),
        Optional.empty()
      )
    );
  }

  private String local(
    final CAStringConstantType constant)
  {
    return this.strings.format(constant);
  }

  private void setAttribute(
    final CAStringConstantType name,
    final Object value)
  {
    this.attributes.put(this.local(name), value.toString());
  }

  @Override
  public String toString()
  {
    return "[CATypePackageUpgradePlanner 0x%s]"
      .formatted(Integer.toUnsignedString(this.hashCode(), 16));
  }
}
