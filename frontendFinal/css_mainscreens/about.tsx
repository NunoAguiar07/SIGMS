import React from "react";
// @ts-ignore
import '../css_styling/PropertiesAbout.css';

// @ts-ignore
const About = ({ onClose }) => {

    return (
        <div className="about-modal">
            <div className="about-content">
                <button className="close-button" onClick={onClose}>
                    ×
                </button>

                <h2>About SIGMS</h2>
                <p>This app was made by:</p>
                <ul>
                    <li>Nuno Aguiar</li>
                    <li>Tomás Martinho</li>
                    <li>Felipe Alvarez</li>
                </ul>
                <p>Now we need to write a bit more!!</p>
            </div>
        </div>
    );
};

export default About;
