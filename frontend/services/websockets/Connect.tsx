import {apiUrl} from "../fetchWelcome";
import {ConnectProps} from "../../types/websocket/ConnectProps";

const endpoint = `${apiUrl}ws/notifications`

export const connect = (connectProps: ConnectProps) =>  {
    const ws = new WebSocket(endpoint);
    ws.onopen = (ev) => {
        connectProps.onConnect(ws, ev)
    } ;
    ws.onclose = (ev) => {
        connectProps.onClose(ws, ev)
    };
    ws.onerror = (ev) => {
        console.log(ev)
    }
    ws.onmessage = (ev) => {
        connectProps.onMessage(ws, ev)
    }
    return ws
}