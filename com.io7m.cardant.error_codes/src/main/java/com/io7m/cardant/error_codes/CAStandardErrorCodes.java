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
package com.io7m.cardant.error_codes;

/**
 * <p>The standard error codes.</p>
 * <p>Note: This file is generated from codes.txt and should not be hand-edited.</p>
 */
public final class CAStandardErrorCodes
{
  private CAStandardErrorCodes()
  {
  }

  private static final CAErrorCode ERROR_API_MISUSE =
    new CAErrorCode("error-api-misuse");

  /**
   * An API was used incorrectly.
   *
   * @return The error code
   */
  public static CAErrorCode errorApiMisuse()
  {
    return ERROR_API_MISUSE;
  }

  private static final CAErrorCode ERROR_AUTHENTICATION =
    new CAErrorCode("error-authentication");

  /**
   * Authentication failed.
   *
   * @return The error code
   */
  public static CAErrorCode errorAuthentication()
  {
    return ERROR_AUTHENTICATION;
  }

  private static final CAErrorCode ERROR_CYCLIC =
    new CAErrorCode("error-cyclic");

  /**
   * A cycle was introduced into a structure that is not supposed to be cyclic.
   *
   * @return The error code
   */
  public static CAErrorCode errorCyclic()
  {
    return ERROR_CYCLIC;
  }

  private static final CAErrorCode ERROR_DUPLICATE =
    new CAErrorCode("error-duplicate");

  /**
   * An object already exists.
   *
   * @return The error code
   */
  public static CAErrorCode errorDuplicate()
  {
    return ERROR_DUPLICATE;
  }

  private static final CAErrorCode ERROR_HTTP_METHOD =
    new CAErrorCode("error-http-method");

  /**
   * The wrong HTTP method was used.
   *
   * @return The error code
   */
  public static CAErrorCode errorHttpMethod()
  {
    return ERROR_HTTP_METHOD;
  }

  private static final CAErrorCode ERROR_IO =
    new CAErrorCode("error-io");

  /**
   * An internal I/O error.
   *
   * @return The error code
   */
  public static CAErrorCode errorIo()
  {
    return ERROR_IO;
  }

  private static final CAErrorCode ERROR_NONEXISTENT =
    new CAErrorCode("error-nonexistent");

  /**
   * A requested object was not found.
   *
   * @return The error code
   */
  public static CAErrorCode errorNonexistent()
  {
    return ERROR_NONEXISTENT;
  }

  private static final CAErrorCode ERROR_NOT_LOGGED_IN =
    new CAErrorCode("error-not-logged-in");

  /**
   * A user is trying to perform an operation without having logged in.
   *
   * @return The error code
   */
  public static CAErrorCode errorNotLoggedIn()
  {
    return ERROR_NOT_LOGGED_IN;
  }

  private static final CAErrorCode ERROR_NO_SUPPORTED_PROTOCOLS =
    new CAErrorCode("error-no-supported-protocols");

  /**
   * The client and server have no supported protocols in common.
   *
   * @return The error code
   */
  public static CAErrorCode errorNoSupportedProtocols()
  {
    return ERROR_NO_SUPPORTED_PROTOCOLS;
  }

  private static final CAErrorCode ERROR_OPERATION_NOT_PERMITTED =
    new CAErrorCode("error-operation-not-permitted");

  /**
   * A generic "operation not permitted" error.
   *
   * @return The error code
   */
  public static CAErrorCode errorOperationNotPermitted()
  {
    return ERROR_OPERATION_NOT_PERMITTED;
  }

  private static final CAErrorCode ERROR_PARSE =
    new CAErrorCode("error-parse");

  /**
   * A parse error was encountered.
   *
   * @return The error code
   */
  public static CAErrorCode errorParse()
  {
    return ERROR_PARSE;
  }

  private static final CAErrorCode ERROR_PROTOCOL =
    new CAErrorCode("error-protocol");

  /**
   * A client sent a broken message of some kind.
   *
   * @return The error code
   */
  public static CAErrorCode errorProtocol()
  {
    return ERROR_PROTOCOL;
  }

  private static final CAErrorCode ERROR_REMOVE_IDENTIFIED_ITEMS =
    new CAErrorCode("error-remove-identified-items");

  /**
   * The items to be removed have serial numbers and cannot be removed as part of a set.
   *
   * @return The error code
   */
  public static CAErrorCode errorRemoveIdentifiedItems()
  {
    return ERROR_REMOVE_IDENTIFIED_ITEMS;
  }

  private static final CAErrorCode ERROR_REMOVE_TOO_MANY_ITEMS =
    new CAErrorCode("error-remove-too-many-items");

  /**
   * An attempt was made to remove more items than actually exist.
   *
   * @return The error code
   */
  public static CAErrorCode errorRemoveTooManyItems()
  {
    return ERROR_REMOVE_TOO_MANY_ITEMS;
  }

  private static final CAErrorCode ERROR_RESOURCE_CLOSE_FAILED =
    new CAErrorCode("error-resource-close-failed");

  /**
   * One or more resources failed to close.
   *
   * @return The error code
   */
  public static CAErrorCode errorResourceCloseFailed()
  {
    return ERROR_RESOURCE_CLOSE_FAILED;
  }

  private static final CAErrorCode ERROR_SECURITY_POLICY_DENIED =
    new CAErrorCode("error-security-policy-denied");

  /**
   * An operation was denied by the security policy.
   *
   * @return The error code
   */
  public static CAErrorCode errorSecurityPolicyDenied()
  {
    return ERROR_SECURITY_POLICY_DENIED;
  }

  private static final CAErrorCode ERROR_SQL_FOREIGN_KEY =
    new CAErrorCode("error-sql-foreign-key");

  /**
   * A violation of an SQL foreign key integrity constraint.
   *
   * @return The error code
   */
  public static CAErrorCode errorSqlForeignKey()
  {
    return ERROR_SQL_FOREIGN_KEY;
  }

  private static final CAErrorCode ERROR_SQL_REVISION =
    new CAErrorCode("error-sql-revision");

  /**
   * An internal SQL database error relating to database revisioning.
   *
   * @return The error code
   */
  public static CAErrorCode errorSqlRevision()
  {
    return ERROR_SQL_REVISION;
  }

  private static final CAErrorCode ERROR_SQL_UNIQUE =
    new CAErrorCode("error-sql-unique");

  /**
   * A violation of an SQL uniqueness constraint.
   *
   * @return The error code
   */
  public static CAErrorCode errorSqlUnique()
  {
    return ERROR_SQL_UNIQUE;
  }

  private static final CAErrorCode ERROR_SQL_UNSUPPORTED_QUERY_CLASS =
    new CAErrorCode("error-sql-unsupported-query-class");

  /**
   * An attempt was made to use a query class that is unsupported.
   *
   * @return The error code
   */
  public static CAErrorCode errorSqlUnsupportedQueryClass()
  {
    return ERROR_SQL_UNSUPPORTED_QUERY_CLASS;
  }

  private static final CAErrorCode ERROR_SQL =
    new CAErrorCode("error-sql");

  /**
   * An internal SQL database error.
   *
   * @return The error code
   */
  public static CAErrorCode errorSql()
  {
    return ERROR_SQL;
  }

  private static final CAErrorCode ERROR_TRASCO =
    new CAErrorCode("error-trasco");

  /**
   * An error raised by the Trasco database versioning library.
   *
   * @return The error code
   */
  public static CAErrorCode errorTrasco()
  {
    return ERROR_TRASCO;
  }

  private static final CAErrorCode ERROR_TYPE_CHECK_FAILED =
    new CAErrorCode("error-type-check-failed");

  /**
   * Type checking failed.
   *
   * @return The error code
   */
  public static CAErrorCode errorTypeCheckFailed()
  {
    return ERROR_TYPE_CHECK_FAILED;
  }

  private static final CAErrorCode ERROR_TYPE_CHECK_FIELD_INVALID =
    new CAErrorCode("error-type-field-invalid");

  /**
   * A field value did not match the provided pattern.
   *
   * @return The error code
   */
  public static CAErrorCode errorTypeCheckFieldInvalid()
  {
    return ERROR_TYPE_CHECK_FIELD_INVALID;
  }

  private static final CAErrorCode ERROR_TYPE_CHECK_FIELD_PATTERN_FAILURE =
    new CAErrorCode("error-type-field-pattern-invalid");

  /**
   * A field pattern was invalid.
   *
   * @return The error code
   */
  public static CAErrorCode errorTypeCheckFieldPatternFailure()
  {
    return ERROR_TYPE_CHECK_FIELD_PATTERN_FAILURE;
  }

  private static final CAErrorCode ERROR_TYPE_CHECK_FIELD_REQUIRED_MISSING =
    new CAErrorCode("error-type-field-required-missing");

  /**
   * A field was required but is missing.
   *
   * @return The error code
   */
  public static CAErrorCode errorTypeCheckFieldRequiredMissing()
  {
    return ERROR_TYPE_CHECK_FIELD_REQUIRED_MISSING;
  }

  private static final CAErrorCode ERROR_TYPE_FIELD_TYPE_NONEXISTENT =
    new CAErrorCode("error-type-field-type-nonexistent");

  /**
   * A field in the type declaration refers to a nonexistent type.
   *
   * @return The error code
   */
  public static CAErrorCode errorTypeFieldTypeNonexistent()
  {
    return ERROR_TYPE_FIELD_TYPE_NONEXISTENT;
  }

  private static final CAErrorCode ERROR_TYPE_REFERENCED =
    new CAErrorCode("error-type-referenced");

  /**
   * The type is referenced by one or more existing items.
   *
   * @return The error code
   */
  public static CAErrorCode errorTypeReferenced()
  {
    return ERROR_TYPE_REFERENCED;
  }

  private static final CAErrorCode ERROR_TYPE_SCALAR_REFERENCED =
    new CAErrorCode("error-type-scalar-referenced");

  /**
   * The scalar type is referenced by one or more existing types/fields.
   *
   * @return The error code
   */
  public static CAErrorCode errorTypeScalarReferenced()
  {
    return ERROR_TYPE_SCALAR_REFERENCED;
  }

  private static final CAErrorCode ERROR_USER_NONEXISTENT =
    new CAErrorCode("error-user-nonexistent");

  /**
   * An attempt was made to reference a user that does not exist.
   *
   * @return The error code
   */
  public static CAErrorCode errorUserNonexistent()
  {
    return ERROR_USER_NONEXISTENT;
  }

  private static final CAErrorCode ERROR_ITEM_STILL_IN_LOCATION =
    new CAErrorCode("error-item-still-in-location");

  /**
   * An item cannot be deleted when instances of it are still present in one or more locations.
   *
   * @return The error code
   */
  public static CAErrorCode errorItemStillInLocation()
  {
    return ERROR_ITEM_STILL_IN_LOCATION;
  }

  private static final CAErrorCode ERROR_LOCATION_NOT_EMPTY =
    new CAErrorCode("error-location-not-empty");

  /**
   * A location cannot be deleted while it still contains one or more items.
   *
   * @return The error code
   */
  public static CAErrorCode errorLocationNotEmpty()
  {
    return ERROR_LOCATION_NOT_EMPTY;
  }

  private static final CAErrorCode ERROR_LOCATION_NON_DELETED_CHILDREN =
    new CAErrorCode("error-location-non-deleted-children");

  /**
   * A location cannot be deleted while it still has non-deleted child locations.
   *
   * @return The error code
   */
  public static CAErrorCode errorLocationNonDeletedChildren()
  {
    return ERROR_LOCATION_NON_DELETED_CHILDREN;
  }

  private static final CAErrorCode ERROR_STOCK_IS_NOT_SET =
    new CAErrorCode("error-stock-is-not-set");

  /**
   * The given stock instance is not a set instance.
   *
   * @return The error code
   */
  public static CAErrorCode errorStockIsNotSet()
  {
    return ERROR_STOCK_IS_NOT_SET;
  }

  private static final CAErrorCode ERROR_STOCK_IS_NOT_SERIAL =
    new CAErrorCode("error-stock-is-not-serial");

  /**
   * The given stock instance is not a serial instance.
   *
   * @return The error code
   */
  public static CAErrorCode errorStockIsNotSerial()
  {
    return ERROR_STOCK_IS_NOT_SERIAL;
  }
}

