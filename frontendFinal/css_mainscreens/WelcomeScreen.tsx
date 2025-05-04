import { ReactElement, JSXElementConstructor, ReactNode, ReactPortal, Key } from "react";
import {backgroundStyle, mainStyle} from "../css_styling/Properties";


// @ts-ignore
export const WelcomeScreen = ({welcome}) => {
    return (
        <div style={backgroundStyle}>

            <div style={{...mainStyle, color: 'white'}}>
                {welcome.title && <h2>{welcome.title}</h2>}
                {welcome.links && welcome.links.map((link: {
                    url: string | undefined;
                    name: string | number | bigint | boolean | ReactElement<unknown, string | JSXElementConstructor<any>> | Iterable<ReactNode> | ReactPortal | Promise<string | number | bigint | boolean | ReactPortal | ReactElement<unknown, string | JSXElementConstructor<any>> | Iterable<ReactNode> | null | undefined> | null | undefined;
                }, index: Key | null | undefined) => (
                    <a key={index} href={link.url} style={{...mainStyle, color: 'white'}}>{link.name}</a>
                ))}
            </div>

        </div>
    )
}