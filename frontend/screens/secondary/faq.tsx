import React from "react";
import '../../css_styling/PropertiesAbout.css';

// @ts-ignore
const FAQ = ({onClose}) => {
    return (
        <div className="about-modal">
        <div className="about-content">
            <button className="close-button" onClick={onClose}>
                ×
            </button>
            <h1>Frequently Asked Questions</h1>
            <div>
                <h2>What is SIGMS?</h2>
                <p>SIGMS is a platform designed to simplify classroom management and enhance the learning experience.</p>
            </div>
            <div>
                <h2>How do I create an account?</h2>
                <p>You can create an account by clicking on the "SIGN IN WITH MICROSOFT" button on the homepage and following the instructions.</p>
            </div>
        </div>
        </div>
    );
}

export default FAQ