import {useAdminEntityDeletion} from "../../hooks/useAdminEntityDeletion";
import LoadingPresentation from "../../screens/auxScreens/LoadingScreen";
import ErrorHandler from "../(public)/error";
import {AdminEntityDeletionScreen} from "../../screens/mainScreens/DeleteEntitiesScreen";


const AdminEntityDeletion = () => {
    const {
        selectedEntity,
        handleEntitySelect,
        // Subject/Class props
        subjects,
        subjectClasses,
        selectedSubject,
        selectedClass,
        searchQuerySubjects,
        setSearchQuerySubjects,
        handleSubjectSelect,
        handleClassSelect,
        // Room props
        rooms,
        selectedRoom,
        searchQueryRooms,
        setSearchQueryRooms,
        handleRoomSelect,
        // Lecture props
        lectures,
        selectedLecture,
        lectureFilter,
        setLectureFilter,
        handleLectureSelect,
        // Common props
        error,
        isLoading,
        handleDelete
    } = useAdminEntityDeletion();

    if (isLoading) {
        return <LoadingPresentation />;
    }

    if (error) {
        return <ErrorHandler errorStatus={error.status} errorMessage={error.message} />;
    }

    return (
        <AdminEntityDeletionScreen
            // Entity selection
            selectedEntity={selectedEntity}
            onEntitySelect={handleEntitySelect}

            // Subject/Class props
            subjects={subjects}
            subjectClasses={subjectClasses}
            selectedSubject={selectedSubject}
            selectedClass={selectedClass}
            searchQuerySubjects={searchQuerySubjects}
            onSearchQuerySubjectsChange={setSearchQuerySubjects}
            onSubjectSelect={handleSubjectSelect}
            onClassSelect={handleClassSelect}

            // Room props
            rooms={rooms}
            selectedRoom={selectedRoom}
            searchQueryRooms={searchQueryRooms}
            onSearchQueryRoomsChange={setSearchQueryRooms}
            onRoomSelect={handleRoomSelect}

            // Lecture props
            lectures={lectures}
            selectedLecture={selectedLecture}
            lectureFilter={lectureFilter}
            onLectureFilterChange={setLectureFilter}
            onLectureSelect={handleLectureSelect}

            // Delete action
            onDelete={handleDelete}
        />
    );
};

export default AdminEntityDeletion;