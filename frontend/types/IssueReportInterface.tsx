import {RoomInterface} from "./RoomInterface";


export interface IssueReportInterface {
    id: number;
    room: RoomInterface;
    userId: number;
    description: string;
    assignedTo?: number;
}

