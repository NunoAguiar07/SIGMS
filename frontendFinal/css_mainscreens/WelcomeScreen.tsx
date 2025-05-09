import React from "react";
import { backgroundStyle, mainStyle } from "../css_styling/Properties";
import '../css_styling/PropertiesWelcome.css'
import Button from "../Utils/Button";
import {Image} from "expo-image";
import {Redirect} from "expo-router";
import MicrosoftAuthButton from "../requests/MicrosoftLogin/Login";
// @ts-ignore
export const WelcomeScreen = ({ welcome }) => {
    return (

        <div className="background-board">
            {/* Logo */}
            <div className="logo-welcomePage">
                <Image source={require("../assets/Logo.webp")} style={{width: 903, height: 516}}></Image>
            </div>
            {/* Slogan */}
            <div className="shape text a-tua-sala-24b9efeac4eb">
                <div className="text-node-html">
                    <p style={{
                        color: '#671b22',
                        fontSize: '55px',
                        fontFamily: '"Roboto Condensed"',
                        fontWeight: 400,
                        whiteSpace: 'pre-wrap'
                    }}>
                        A tua sala de aula, mais simples. {welcome.title}
                    </p>
                </div>
            </div>
            <div className="rectangle-login">
                <MicrosoftAuthButton/>
            </div>
            <Button text="Register" onClick={() => {
            }} className={"rectangle-register"}></Button>


            <div style={{
                position: 'absolute',
                bottom: '0%',
                left: 0,
                right: 0,
                display: 'flex',
                gap: '20px'
            }}>
                <Button text="About" onClick={() => {
                }} className="about-faq-privacy-button"/>
                <Button text="FAQ" onClick={() => {
                }} className="about-faq-privacy-button"/>
                <Button text="Privacy" onClick={() => {
                }} className="about-faq-privacy-button"/>
            </div>
        </div>
    );
};
