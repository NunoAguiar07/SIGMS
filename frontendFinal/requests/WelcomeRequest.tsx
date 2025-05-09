import {ErrorUriParser} from "../Utils/UriParser";

export const apiUrl = 'http://localhost:8080/api'

/**
 * Const "GetData" represents the information we want to represent on the home page.
 * @param setWelcome represents the information to represent in the home page.
 * @param setError represents the error if happens in the screen.
 * @return the information of the home provided by the server.
 */
// @ts-ignore
export const GetData = (setWelcome, setError) => {
    return async () => {
        try {
            const response = await fetch(apiUrl);
            const data = await response.json()
            if (!response.ok) {
                throw { status: response.status, message: ErrorUriParser(data.type)}
            }
            setWelcome(data)
        } catch (error) {
            setError(error)
        }
    }
}
