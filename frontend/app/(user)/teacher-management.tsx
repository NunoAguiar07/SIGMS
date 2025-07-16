import { TeacherManagementScreen } from "../../screens/mainScreens/TeacherManagementScreen";
import LoadingPresentation from "../../screens/auxScreens/LoadingScreen";
import ErrorHandler from "../(public)/error";
import { useTeacherManagement } from "../../hooks/useTeacherManagement";

const TeacherManagement = () => {
    const {
        teachers,
        currentPage,
        hasNext,
        isLoading,
        isRoomsLoading,
        error,
        handleAssign,
        handleUnassign,
        handleNext,
        handlePrevious,
        officeRooms,
        onNextRoomPage,
        onPreviousRoomPage,
        hasNextRoomPage,
        currentRoomPage,
        modalVisible,
        openModal,
        closeModal,
        handleSelectRoom,
    } = useTeacherManagement();

    if (isLoading) return <LoadingPresentation />;
    if (error) return <ErrorHandler errorStatus={error.status} errorMessage={error.message} />;

    return (
        <TeacherManagementScreen
            teachers={teachers}
            currentPage={currentPage}
            hasNext={hasNext}
            onAssign={handleAssign}
            onUnassign={handleUnassign}
            onNext={handleNext}
            onPrevious={handlePrevious}
            officeRooms={officeRooms}
            onNextRoomPage={onNextRoomPage}
            onPreviousRoomPage={onPreviousRoomPage}
            hasNextRoomPage={hasNextRoomPage}
            currentRoomPage={currentRoomPage}

            modalVisible={modalVisible}
            openModal={openModal}
            closeModal={closeModal}
            handleSelectRoom={handleSelectRoom}
            isRoomsLoading={isRoomsLoading}
        />
    );
};

export default TeacherManagement;
