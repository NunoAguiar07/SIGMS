import ErrorHandler from "../(public)/error";
import TechnicianUnassignedIssuesScreen from "../../screens/mainScreens/UnassignedIssuesScreen";
import LoadingPresentation from "../../screens/auxScreens/LoadingScreen";
import {useUnassignedIssues} from "../../hooks/useUnnasignedIssues";
import {BackgroundImage} from "../../screens/components/BackgroundImage";


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
        <BackgroundImage>
            <TechnicianUnassignedIssuesScreen
                issues={issues}
                currentPage={currentPage}
                onNext={goToNextPage}
                onPrevious={goToPreviousPage}
                onAssign={assignIssue}
                hasNext={hasNext}
            />
        </BackgroundImage>
    );
};

export default TechnicianUnassignedIssues;