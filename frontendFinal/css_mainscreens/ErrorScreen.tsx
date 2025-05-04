import {backgroundStyle, buttonStyle, headerStyle, infoStyle} from "../css_styling/Properties";


/**
 * Const "ErrorScreen" representing the page when an error happens in the application.
 * @param errorStatus represents the error code that happens.
 * @param errorMessage represents the error message we want to represent to the user.
 * @param goBack represent the button/function to do when we want to go back before the error.
 * @return the error page.
 */
// @ts-ignore
export const ErrorScreen = ({errorStatus, errorMessage, goBack}) => {
    return (
        <div style={backgroundStyle}>
            <header style={headerStyle}>
                <h1>Error: {errorStatus}</h1>
            </header>
            <div style={{ ...infoStyle, color: 'red' }}>{errorMessage}</div>
            <button style={buttonStyle} onClick={goBack}>Go Back</button>
        </div>
    )
}