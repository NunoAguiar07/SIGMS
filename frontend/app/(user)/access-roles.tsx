import {AccessRolesScreen} from "../../screens/mainScreens/AccessRolesScreen";
import ErrorHandler from "../(public)/error";
import LoadingPresentation from "../../screens/auxScreens/LoadingScreen";
import {useAccessRoles} from "../../hooks/useAccessRoles";
import {BackgroundImage} from "../../screens/components/BackgroundImage";


const AccessRoles = () => {
    const {
        approvals,
        currentPage,
        hasNext,
        isLoading,
        error,
        handleApprove,
        handleReject,
        handleNext,
        handlePrevious
    } = useAccessRoles();

    if (isLoading) return <LoadingPresentation />;
    if (error) return <ErrorHandler errorStatus={error.status} errorMessage={error.message} />;

    return (
        <BackgroundImage>
            <AccessRolesScreen
                approvals={approvals}
                currentPage={currentPage}
                hasNext={hasNext}
                onApprove={handleApprove}
                onReject={handleReject}
                onNext={handleNext}
                onPrevious={handlePrevious}
            />
        </BackgroundImage>
    );
};

export default AccessRoles;