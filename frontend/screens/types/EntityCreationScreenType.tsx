import {SubjectInterface} from "../../types/SubjectInterface";
import {FormCreateEntityValues} from "../../types/welcome/FormCreateEntityValues";
import {SchoolClassInterface} from "../../types/SchoolClassInterface";
import {RoomInterface} from "../../types/RoomInterface";

export type EntityType = 'subject' | 'class' | 'room' | 'lecture' | null;

export interface EntityCreationScreenType {
    selectedEntity: EntityType;
    onEntitySelect: (entity: EntityType) => void;
    formValues: FormCreateEntityValues;
    setFormValues: (values: FormCreateEntityValues) => void;
    onSubmit: () => void;
    subjects: SubjectInterface[];
    subjectClasses: SchoolClassInterface[];
    rooms: RoomInterface[];
    searchQuery: string;
    setSearchQuery: (query: string) => void;
    isLoading: boolean;
}