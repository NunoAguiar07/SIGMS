import React from "react";
import '../../css_styling/PropertiesAbout.css';
// @ts-ignore
const Privacy = ({onClose}) => {
    return (
        <div className="about-modal">
            <div className="about-content">
                <button className="close-button" onClick={onClose}>
                    ×
                </button>
                <h1>Privacy Policy</h1>
                <p>This is the privacy policy for SIGMS.</p>
                <p>We take your privacy seriously and are committed to protecting your personal information.</p>
            </div>
        </div>
    );
}

export default Privacy

