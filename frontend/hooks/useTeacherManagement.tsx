import { useCallback, useState } from "react";
import { useFocusEffect } from "expo-router";
import { ParsedError } from "../types/errors/ParseErrorTypes";
import { handleAxiosError } from "../utils/HandleAxiosError";

import { useAlert } from "./notifications/useAlert";
import { fetchTeachers } from "../services/authorized/FetchTeachers";
import { TeacherUser } from "../types/teacher/TeacherUser";
import { unassignTeacherFromOffice } from "../services/authorized/RequestUnassignOffice";
import { assignTeacherToOffice } from "../services/authorized/RequestAssignOffice";
import { RoomInterface } from "../types/RoomInterface";
import { fetchRooms } from "../services/authorized/FetchRooms";

export const useTeacherManagement = () => {
    const [teachers, setTeachers] = useState<TeacherUser[]>([]);
    const [officeRooms, setOfficeRooms] = useState<RoomInterface[]>([]);
    const [currentPage, setCurrentPage] = useState(0);
    const [hasNext, setHasNext] = useState(false);
    const [currentRoomPage, setCurrentRoomPage] = useState(0);
    const [hasNextRoomPage, setHasNextRoomPage] = useState(false);
    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState<ParsedError | null>(null);
    const [modalVisible, setModalVisible] = useState(false);
    const [selectedTeacherId, setSelectedTeacherId] = useState<number | null>(null);
    const [isRoomsLoading, setIsRoomsLoading] = useState(false);

    const LIMIT = 7;
    const showAlert = useAlert();

    const fetchTeachersHook = async (page: number) => {
        setIsLoading(true);
        setError(null);
        const offset = page * LIMIT;
        try {
            const data = await fetchTeachers(LIMIT, offset);
            setTeachers(data);
            setHasNext(data.length === LIMIT);
        } catch (err) {
            setError(handleAxiosError(err));
        } finally {
            setIsLoading(false);
        }
    };


    const fetchRoomsHook = async (page: number) => {
        setIsRoomsLoading(true);
        setError(null);
        const offset = page * LIMIT;
        try {
            const data = await fetchRooms("", "OFFICE", LIMIT, offset);
            setOfficeRooms(data);
            setHasNextRoomPage(data.length === LIMIT);
        } catch (err) {
            setError(handleAxiosError(err));
        } finally {
            setIsRoomsLoading(false);
        }
    };


    useFocusEffect(
        useCallback(() => {
            fetchTeachersHook(currentPage);
            fetchRoomsHook(currentRoomPage);
        }, [currentPage, currentRoomPage])
    );

    // Modal handlers moved inside hook:
    const openModal = (teacherId: number) => {
        setSelectedTeacherId(teacherId);
        setModalVisible(true);
    };

    const closeModal = () => {
        setSelectedTeacherId(null);
        setModalVisible(false);
    };

    const handleSelectRoom = (roomId: number) => {
        if (selectedTeacherId !== null) {
            handleAssign(selectedTeacherId, roomId);
            closeModal();
        }
    };

    const handleAssign = async (teacherId: number, roomId: number) => {
        try {
            await assignTeacherToOffice(roomId, teacherId);
            showAlert("Success", "Office assigned.");
            fetchTeachersHook(currentPage);
        } catch (err) {
            setError(handleAxiosError(err));
        }
    };

    const handleUnassign = async (teacherId: number, roomId: number) => {
        try {
            await unassignTeacherFromOffice(roomId, teacherId);
            showAlert("Success", "Office unassigned.");
            fetchTeachersHook(currentPage);
        } catch (err) {
            setError(handleAxiosError(err));
        }
    };

    const handleNextTeacherPage = () => setCurrentPage((prev) => prev + 1);
    const handlePreviousTeacherPage = () => setCurrentPage((prev) => Math.max(prev - 1, 0));

    const handleNextRoomPage = () => setCurrentRoomPage((prev) => prev + 1);
    const handlePreviousRoomPage = () => setCurrentRoomPage((prev) => Math.max(prev - 1, 0));

    return {
        teachers,
        currentPage,
        hasNext,
        isLoading,
        isRoomsLoading,         // <---- new loading state for rooms
        error,
        handleAssign,
        handleUnassign,
        handleNext: handleNextTeacherPage,
        handlePrevious: handlePreviousTeacherPage,
        officeRooms,
        onNextRoomPage: handleNextRoomPage,
        onPreviousRoomPage: handlePreviousRoomPage,
        hasNextRoomPage,
        currentRoomPage,
        modalVisible,
        openModal,
        closeModal,
        handleSelectRoom,
    };
};
