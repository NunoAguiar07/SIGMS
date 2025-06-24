import {RoomInterface} from "../../types/RoomInterface";


export interface CreateReportScreenType {
    rooms: RoomInterface[];
    issues : any[]
    selectedRoom: RoomInterface | null;
    reportText: string;
    searchQuery: string;
    onSearchChange: (text: string) => void;
    onRoomSelect: (room: RoomInterface) => void;
    onReportTextChange: (text: string) => void;
    onSubmitReport: (roomId: number, description: string) => void;
}