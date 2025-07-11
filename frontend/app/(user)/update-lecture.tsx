import ErrorHandler from "../(public)/error";
import {UpdateLectureScreen} from "../../screens/mainScreens/UpdateLectureScreen";
import {useUpdateLecture} from "../../hooks/useUpdateLecture";


const UpdateLecture = () => {
    const {
        lectures,
        selectedLecture,
        onLectureSelect,
        onScheduleChange,
        onSaveSchedule,
        error,
        isSaving,
        rooms,
        setSearchQueryRoom,
        searchQueryRoom,
        selectedRoom,
        handleRoomSelect,
        setEffectiveFromText,
        setEffectiveUntilText,
        effectiveFromText,
        effectiveUntilText,
        page,
        lectureFilter,
        setLectureFilter,
        subjects,
        classes,
        onGetAllLectures,
        handleOnSubjectSelect,
        handleClassSelect,
        searchQuerySubjects,
        setSearchQuerySubjects,
        handleNext,
        handlePrevious
    } = useUpdateLecture();

    if (error) {
        return <ErrorHandler errorStatus={error.status} errorMessage={error.message} />;
    }

    return (
        <UpdateLectureScreen
            lectures={lectures}
            selectedLecture={selectedLecture}
            onLectureSelect={onLectureSelect}
            onScheduleChange={onScheduleChange}
            onSaveSchedule={onSaveSchedule}
            isSaving={isSaving}
            rooms={rooms}
            setSearchQueryRoom={setSearchQueryRoom}
            searchQueryRoom={searchQueryRoom}
            selectedRoom={selectedRoom}
            handleRoomSelect={handleRoomSelect}
            setEffectiveFromText={setEffectiveFromText}
            setEffectiveUntilText={setEffectiveUntilText}
            effectiveFromText={effectiveFromText}
            effectiveUntilText={effectiveUntilText}
            page={page}
            lectureFilter={lectureFilter}
            setLectureFilter={setLectureFilter}
            subjects={subjects}
            classes={classes}
            onGetAllLectures={onGetAllLectures}
            handleOnSubjectSelect={handleOnSubjectSelect}
            handleClassSelect={handleClassSelect}
            searchQuerySubjects={searchQuerySubjects}
            setSearchQuerySubjects={setSearchQuerySubjects}
            handleNext={handleNext}
            handlePrevious={handlePrevious}
        />
    );
};

export default UpdateLecture;