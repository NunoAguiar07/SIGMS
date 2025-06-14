export interface ConnectProps {
    onConnect: (ws: WebSocket, ev: Event) => void;
    onMessage: (ws: WebSocket, ev: MessageEvent) => void;
    onClose: (ws: WebSocket, ev: CloseEvent) => void;
}