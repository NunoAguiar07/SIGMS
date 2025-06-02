import ErrorHandler from "../../(public)/error";
import {AssignedIssuesScreen} from "../../../screens/issues/AssignedIssuesScreen";
import {useAssignedIssues} from "../../../hooks/useAssignedIssues";
import LoadingPresentation from "../../../screens/auxScreens/LoadingScreen";


const TechnicianAssignedIssues = () => {
    const {
        issues,
        error,
        isLoading,
        page,
        editingId,
        editedDescription,
        setEditedDescription,
        hasNext,
        handleStartEditing,
        handleCancelEditing,
        handleUpdate,
        handleFix,
        handleUnassigned,
        handleNext,
        handlePrevious
    } = useAssignedIssues();

    if(isLoading) {
        return <LoadingPresentation />
    }

    if (error) {
        return <ErrorHandler errorStatus={error.status} errorMessage={error.message} />;
    }

    return (
        <AssignedIssuesScreen
            issues={issues}
            currentPage={page}
            hasNext={hasNext}
            onNext={handleNext}
            onPrevious={handlePrevious}
            onFix={handleFix}
            onEdit={handleStartEditing}
            onUpdate={handleUpdate}
            onUnassigned={handleUnassigned}
            onCancelEdit={handleCancelEditing}
            editingId={editingId}
            editedDescription={editedDescription}
            setEditedDescription={setEditedDescription}
        />
    );
};

export default TechnicianAssignedIssues;