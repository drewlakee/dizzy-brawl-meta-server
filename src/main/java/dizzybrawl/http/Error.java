package dizzybrawl.http;

public enum Error {

    // JSON error reasons
    EMPTY_BODY,

    // Query
    EMPTY_QUERY_PARAMETER,

    // Login error reasons
    INVALID_PASSWORD,

    // Data error reasons
    INVALID_QUERY_PARAMETER_FORMAT,

    // Database error reasons
    ALREADY_EXIST_AT_DATABASE,
    DOESNT_EXIST_AT_DATABASE,
}
