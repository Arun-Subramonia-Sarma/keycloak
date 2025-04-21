package usa_policy

# Default deny
default allow = false

# Allowed countries
allowed_countries = ["India", "America", "Africa"]

# Allowed tags
allowed_tags = ["tag1", "tag2", "tag3"]

# Allow if the country is in the allowed list
allow if {
  # Check if the country from input is in our allowed countries list
  input.country == allowed_countries[_]
}

# Allow if the tag is in the allowed list
allow if {
  # Check if any tag from input is in our allowed tags list
  input.tags[_] == allowed_tags[_]
}

# Allow if both country and at least one tag match
allow if {
  # Country must be allowed
  input.country == allowed_countries[_]
  
  # And at least one tag must be allowed
  some i
  input.tags[i] == allowed_tags[_]
}