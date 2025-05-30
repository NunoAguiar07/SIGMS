
/**
 * Function "UriParser" represent the passing a URI replacing an "/api" to a empty string getting a new uri.
 * @param uri represent the URI we will be searching.
 */
export function UriParser(uri: string) {
    return uri.replace("/api", "")
}

/**
 * Function to replace the error URI to a correct one.
 * @param uri represent the uri of the errors.
 */
export function ErrorUriParser(uri: string) {
    return uri.replace("https://example.com/probs/", "")
}