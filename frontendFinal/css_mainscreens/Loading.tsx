import React from "react";
import "../css_styling/loading.css";
import {Image} from "expo-image";

/**
 * Const "Loadingpresentation" representing the loading screen when we are loading something.
 * @return the new page for the loading screen.
 */
const LoadingPresentation = () => {
    return (
        <div className="loading-container">
            <div className="loading-logo">
                <Image source={require("../assets/Logo.webp")} style={{width: 903, height: 516}}></Image>
            </div>
            <p className="loading-subtext">A carregar, por favor aguarde...</p>
        </div>
    );
};
export default LoadingPresentation;
