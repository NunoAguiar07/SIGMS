import ErrorHandler from "../(public)/error";
import {UpdateLectureScreen} from "../../screens/UpdateLectureScreen";
import {useUpdateLecture} from "../../hooks/UseUpdateLecture";

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
        setSearchQuery,
        searchQuery,
        selectedRoom,
        handleRoomSelect,
        effectiveFrom,
        setEffectiveFrom,
        effectiveUntil,
        setEffectiveUntil,
        setEffectiveFromText,
        setEffectiveUntilText,
        effectiveFromText,
        effectiveUntilText
    } = useUpdateLecture();

    if (error) {
        return <ErrorHandler errorStatus={error.status} errorMessage={error.message} />;
    }

    return (
        <BackgroundImage>
            <UpdateLectureScreen
                lectures={lectures}
                selectedLecture={selectedLecture}
                newSchedule={selectedLecture}
                onLectureSelect={onLectureSelect}
                onScheduleChange={onScheduleChange}
                onSaveSchedule={onSaveSchedule}
                isSaving={isSaving}
                rooms={rooms}
                setSearchQuery={setSearchQuery}
                searchQuery={searchQuery}
                selectedRoom={selectedRoom}
                handleRoomSelect={handleRoomSelect}
                effectiveFrom={effectiveFrom}
                setEffectiveFrom={setEffectiveFrom}
                effectiveUntil={effectiveUntil}
                setEffectiveUntil={setEffectiveUntil}
                setEffectiveFromText={setEffectiveFromText}
                setEffectiveUntilText={setEffectiveUntilText}
                effectiveFromText={effectiveFromText}
                effectiveUntilText={effectiveUntilText}
            />
        </BackgroundImage>
    );
};

export default UpdateLecture