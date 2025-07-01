import {IssueReportInterface} from "../../types/IssueReportInterface";
import {RoomInterface} from "../../types/RoomInterface";

export interface RoomReportsScreenType {
    reports: IssueReportInterface[];
    room: RoomInterface | null;
}