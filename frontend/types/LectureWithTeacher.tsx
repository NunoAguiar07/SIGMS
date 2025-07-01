import {Lecture} from "./calendar/Lecture";
import {TeacherUser} from "./teacher/TeacherUser";

export type LectureWithTeacher = {
    lecture: Lecture;
    teacher: TeacherUser[];
}
