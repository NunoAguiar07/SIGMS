import React from "react";
import { backgroundStyle, mainStyle } from "../css_styling/Properties";
import '../css_styling/PropertiesWelcome.css'
import Button from "../Utils/Button";
import {Image} from "expo-image";
// @ts-ignore
export const WelcomeScreen = ({ welcome }) => {
    return (

        <div className="background-board">
            {/* Logo */}
            <div className="logo-welcomePage">
            <Image source={require("../assets/Logo.webp")} style={{width:903, height:516}}></Image>
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
            <div>
           <Button text="Login" onClick={() => {}} className={"rectangle-24baecca1439"}></Button>
            </div>




            {/* About / FAQ / Privacy */}
            <div className="shape text about-FAQ-Privacy">
                <div className="text-node-html">
                    <p style={{
                        color: '#b8b2ab',
                        fontSize: '40px',
                        fontFamily: '"Roboto Condensed"',
                        fontWeight: 400,
                        whiteSpace: 'pre'
                    }}>
                        About FAQ Privacy
                    </p>
                </div>
            </div>
        </div>
    );
};
