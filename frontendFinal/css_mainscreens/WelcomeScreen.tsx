import React, {useState} from "react";
import '../css_styling/PropertiesWelcome.css'
import Button from "../Utils/Button";
import {Image} from "expo-image";
import MicrosoftAuthButton from "../requests/Microsoft/Login";
import About from "./about";
import Privacy from "./privacy";
import FAQ from "./faq";
// @ts-ignore
export const WelcomeScreen = ({ welcome }) => {
    const [showAbout, setShowAbout] = useState(false);
    const [showFAQ, setShowFAQ] = useState(false);
    const [showPrivacy, setShowPrivacy] = useState(false);
    return (

        <div className="background-board">
            {showAbout && <About onClose={() => setShowAbout(false)} />}
            {showFAQ && <FAQ onClose={() => setShowFAQ(false)} />}
            {showPrivacy && <Privacy onClose={() => setShowPrivacy(false)} />}
            {/* Logo */}
            <div className="logo-welcomePage">
                <Image source={require("../assets/Logo.webp")} style={{width: 903, height: 516 }}></Image>
            </div>
            {/* Slogan */}
            <div className="shape text a-tua-sala-24b9efeac4eb">
                <div className="text-node">
                    <p style={{
                        color: '#671b22',
                        fontSize: '55px',
                        fontFamily: '"Roboto Condensed"',
                        fontWeight: 400,
                        whiteSpace: 'pre-wrap',
                        textAlign: 'center',
                    }}><p>
                        {welcome.title}
                    </p>
                        A tua sala de aula, mais simples.
                    </p>
                </div>
            </div>
            <div className="microsoft-auth-button">
                <MicrosoftAuthButton/>
            </div>



            <div style={{
                position: 'absolute',
                bottom: '0%',
                left: 0,
                right: 0,
                display: 'flex',
                gap: '20px'
            }}>
                <Button text="About" onClick={() => setShowAbout(true)} className="about-faq-privacy-button"/>
                <Button text="FAQ" onClick={() => setShowFAQ(true)} className="about-faq-privacy-button"/>
                <Button text="Privacy" onClick={() => setShowPrivacy(true)} className="about-faq-privacy-button"/>
            </div>
        </div>
    );
};
