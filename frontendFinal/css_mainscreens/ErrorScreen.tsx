import {Image} from "expo-image";
import React from "react";
import "../css_styling/Error.css"


/**
 * Const "ErrorScreen" representing the page when an error happens in the application.
 * @param errorStatus represents the error code that happens.
 * @param errorMessage represents the error message we want to represent to the user.
 * @param goBack represent the button/function to do when we want to go back before the error.
 * @return the error page.
 */
// @ts-ignore
export const ErrorScreen = ({ errorStatus, errorMessage, goBack }) => {
    return (
        <div className="error-container">
            <div className="error-logo">
                <Image source={require("../assets/Logo.webp")} style={{width: 903, height: 516}}></Image>
            </div>
            <h1 className="error-title">Error {errorStatus}</h1>
            <p className="error-message">{errorMessage}</p>
            <button className="error-button" onClick={goBack}>Go back</button>
        </div>
    );
};