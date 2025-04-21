package example

# Default deny
default allow = false

# Allow GET requests to public endpoints
allow if {
    input.method == "GET"
    input.path[0] == "public"
}

# Allow authenticated users to access their own data
allow if {
    input.method == "GET"
    input.path[0] == "users"
    input.path[1] == input.user.id
}

# Allow admins to do anything
allow if {
    input.user.role == "admin"
}