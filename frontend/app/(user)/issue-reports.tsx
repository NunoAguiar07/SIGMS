import ErrorHandler from "../(public)/error";
import TechnicianUnassignedIssuesScreen from "../../screens/mainScreens/UnassignedIssuesScreen";
import LoadingPresentation from "../../screens/auxScreens/LoadingScreen";
import {useUnassignedIssues} from "../../hooks/useUnnasignedIssues";


const TechnicianUnassignedIssues = () => {
    const {
        issues,
        error,
        loading,
        currentPage,
        hasNext,
        assignIssue,
        goToNextPage,
        goToPreviousPage,
    } = useUnassignedIssues();

    if (loading) return <LoadingPresentation />;
    if (error) return <ErrorHandler errorStatus={error.status} errorMessage={error.message} />;

    return (
        <TechnicianUnassignedIssuesScreen
            issues={issues}
            isLoading={loading}
            currentPage={currentPage}
            onNext={goToNextPage}
            onPrevious={goToPreviousPage}
            onAssign={assignIssue}
            hasNext={hasNext}
        />
    );
};

export default TechnicianUnassignedIssues;