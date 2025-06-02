import {SubjectInterface} from "../../types/SubjectInterface";
import {SchoolClassInterface} from "../../types/SchoolClassInterface";


export interface JoinSubjectScreenType {
    subjects: SubjectInterface[];
    schoolClasses: SchoolClassInterface[];
    selectedSubject: SubjectInterface | null;
    searchQuery: string;
    onSearchChange: (text: string) => void;
    onSubjectSelect: (subject: SubjectInterface) => void;
    onJoinClass: (subjectId: number, classId: number) => void;
    loadingClasses: boolean;
}