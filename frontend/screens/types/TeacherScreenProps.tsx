import {RoomInterface} from "../../types/RoomInterface";
import {TeacherUser} from "../../types/teacher/TeacherUser";

export interface TeacherScreenProps {
    teachers: TeacherUser[];
    onAssign: (teacherId: number, roomId: number) => void;
    onUnassign: (teacherId: number, roomId: number) => void;
    onNext: () => void;
    onPrevious: () => void;
    currentPage: number;
    hasNext: boolean;
    officeRooms: RoomInterface[];
    onNextRoomPage: () => void;
    onPreviousRoomPage: () => void;
    hasNextRoomPage: boolean;
    currentRoomPage: number;
    modalVisible: boolean;
    openModal: (teacherId: number) => void;
    closeModal: () => void;
    handleSelectRoom: (roomId: number) => void;
    isRoomsLoading: boolean;
}
