#
# Copyright © 2023 Mark Raynsford <code@io7m.com> https://www.io7m.com
#
# Permission to use, copy, modify, and/or distribute this software for any
# purpose with or without fee is hereby granted, provided that the above
# copyright notice and this permission notice appear in all copies.
#
# THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
# WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
# MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY
# SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
# WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
# ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR
# IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
#

[medrina 1 0]

#------------------------------------------------------------------------
# Admins
#

[allow-immediately
  [subject [with-any-roles inventory.admin]]
  [object   true]
  [action   true]
]

#------------------------------------------------------------------------
# Items
#

[allow
  [subject [with-any-roles inventory.items.writer]]
  [object  [with-type inventory.items]]
  [action  [with-name write]]
]

[allow
  [subject [with-any-roles inventory.items.writer]]
  [object  [with-type inventory.items]]
  [action  [with-name delete]]
]

[allow
  [subject [with-any-roles inventory.items.reader]]
  [object  [with-type inventory.items]]
  [action  [with-name read]]
]

#------------------------------------------------------------------------
# Tags
#

[allow
  [subject [with-any-roles inventory.tags.writer]]
  [object  [with-type inventory.tags]]
  [action  [with-name write]]
]

[allow
  [subject [with-any-roles inventory.tags.writer]]
  [object  [with-type inventory.tags]]
  [action  [with-name delete]]
]

[allow
  [subject [with-any-roles inventory.tags.reader]]
  [object  [with-type inventory.tags]]
  [action  [with-name read]]
]

#------------------------------------------------------------------------
# Files
#

[allow
  [subject [with-any-roles inventory.files.writer]]
  [object  [with-type inventory.files]]
  [action  [with-name write]]
]

[allow
  [subject [with-any-roles inventory.files.writer]]
  [object  [with-type inventory.files]]
  [action  [with-name delete]]
]

[allow
  [subject [with-any-roles inventory.files.reader]]
  [object  [with-type inventory.files]]
  [action  [with-name read]]
]

#------------------------------------------------------------------------
# Locations
#

[allow
  [subject [with-any-roles inventory.locations.writer]]
  [object  [with-type inventory.locations]]
  [action  [with-name write]]
]

[allow
  [subject [with-any-roles inventory.locations.writer]]
  [object  [with-type inventory.locations]]
  [action  [with-name delete]]
]

[allow
  [subject [with-any-roles inventory.locations.reader]]
  [object  [with-type inventory.locations]]
  [action  [with-name read]]
]
