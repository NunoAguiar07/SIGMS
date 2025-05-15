
import React from 'react'

/**
 * Const "gamebackgroundStyle" resposnible to load and represent the image of the background when we are playing the game.
 */
export const gamebackgroundStyle: React.CSSProperties = {
    background: `url(https://i.pinimg.com/originals/e4/a6/b9/e4a6b92746b595562938d01b6a5e4445.gif) center center fixed`,
    backgroundSize: 'cover',
    color: '#fff',
    fontFamily: 'Arial, sans-serif',
    minHeight: '100vh',
    display: 'flex',
    flexDirection: 'column',
    justifyContent: 'center',
    alignItems: 'center',
    margin: 0,
    padding: 0,
    textAlign: 'center',
}

/**
 * Const "backgroundStyle" representing the gif used on the background of the application.
 */
export const backgroundStyle: React.CSSProperties = {
    background: `url(https://i.pinimg.com/originals/e4/a6/b9/e4a6b92746b595562938d01b6a5e4445.gif) center center fixed`,
    backgroundSize: 'cover',
    color: '#fff',
    fontFamily: 'Arial, sans-serif',
    minHeight: '100%',
    minWidth: '1024px',
    width: '100%',
    height: 'auto',
    position: 'fixed',
    top: 0,
    left: 0,
    display: 'flex',
    flexDirection: 'column',
    justifyContent: 'center',
    alignItems: 'center',
    margin: 0,
    padding: 0,
    textAlign: 'center',
}

/**
 * Const "infoStyle" represent the style used on the information.
 */
export const infoStyle: React.CSSProperties = {
    margin: '20px 0',
    fontSize: '20px',
}

/**
 * Const "headerStyle" responsible for the style used in the headers of each page in this case like a title style.
 */
export const headerStyle: React.CSSProperties = {
    padding: '20px',
    backgroundColor: 'rgba(0, 0, 0, 0.7)',
    borderBottom: '0px solid #fff',
    boxShadow: '0 2px 4px rgba(0, 0, 0, 0.1)',

}

/**
 * Const "linkStyle" responsible gor the style used in the link information.
 */
export const linkStyle: React.CSSProperties = {
    textDecoration: 'none',
    color: '#fff',
    margin: '10px',
    padding: '10px 20px',
    borderRadius: '5px',
    backgroundColor: '#3498db',
    transition: 'background-color 0.3s ease',
    cursor: 'pointer',
}

/**
 * Const "linkHoverStyle" responsible for the style of the hover in the links, in this case buttons that have a link associated.
 */
export const linkHoverStyle: React.CSSProperties = {
    backgroundColor: '#2074a0',
}

/**
 * Const "navStyle" responsible for the style we use to represent the group of link buttons for example.
 */
export const navStyle = {
    marginTop: '20px',
    marginBottom: '20px',
    display: 'flex',
    justifyContent: 'center',
}

/**
 * Const "mainStyle" responsible for the main style used in the application.
 */
export const mainStyle = {
    padding: '10px',
    color: '#fff',
    fontSize: '20px',
    backgroundColor: 'rgba(0,0,0,0.5)',
}

/**
 * Const "buttonStyle" responsible for the style of the buttons used in the application.
 */
export const buttonStyle: React.CSSProperties = {
    margin: '10px',
    padding: '10px 20px',
    borderRadius: '5px',
    backgroundColor: '#3498db',
    color: '#fff',
    cursor: 'pointer',
    border: 'none',
}