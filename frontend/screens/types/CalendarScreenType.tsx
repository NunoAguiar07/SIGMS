
import {LectureWithTeacher} from "../../types/LectureWithTeacher";
export interface CalendarScreenType {
    schedule: LectureWithTeacher[];
    onClickProfile : (id:number) => void;
    onClickRoom : (id:number) => void;
}