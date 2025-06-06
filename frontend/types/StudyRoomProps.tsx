import {StudyRoomCapacity} from "./StudyRoomCapacity";
import {Dispatch, SetStateAction} from "react";

export interface StudyRoomProps {
    studyRooms: StudyRoomCapacity[];
    forceUpdate: Dispatch<SetStateAction<boolean>>;
}