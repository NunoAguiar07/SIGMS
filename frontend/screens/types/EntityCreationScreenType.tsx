import {SubjectInterface} from "../../types/SubjectInterface";
import {FormCreateEntityValues} from "../../types/welcome/FormCreateEntityValues";
import {SchoolClassInterface} from "../../types/SchoolClassInterface";
import {RoomInterface} from "../../types/RoomInterface";

export type EntityType = 'Subject' | 'Class' | 'Room' | 'Lecture' | null;

export interface EntityCreationScreenType {
    selectedEntity: EntityType;
    onEntitySelect: (entity: EntityType) => void;
    handleEntitySelect: (entity: EntityType) => void;
    formValues: FormCreateEntityValues;
    setFormValues: (values: FormCreateEntityValues) => void;
    onSubmit: () => void;
    subjects: SubjectInterface[];
    subjectClasses: SchoolClassInterface[];
    rooms: RoomInterface[];
    searchQuerySubjects: string;
    setSearchQuerySubjects: (query: string) => void;
    searchQueryRooms: string;
    setSearchQueryRooms: (query: string) => void;
    onItemSelect: (item: any) => void;
}
