import {backgroundStyle, headerStyle} from "../css_styling/Properties";


/**
 * Const "LoadingApresentation" representing the loading screen when we are loading something.
 * @return the new page for the loading screen.
 */
export const LoadingApresentation = () => {
    return (
        <div style={backgroundStyle}>
            <header style={headerStyle}>
                <h1>Loading...</h1>
            </header>
        </div>
    )
}