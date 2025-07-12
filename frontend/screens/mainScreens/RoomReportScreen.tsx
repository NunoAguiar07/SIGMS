import {RoomReportsScreenType} from "../types/RoomReportScreenType";
import {IssuesList} from "./CreateReportScreen";
import {Card, CenteredContainer} from "../css_styling/common/NewContainers";
import {Subtitle} from "../css_styling/common/Typography";


export const RoomReportsScreen = ({ reports, room }: RoomReportsScreenType) => {
    if(room != null) return (
        <IssuesList selectedRoom={room} issues={reports}>
        </IssuesList>
    );
    return (
        <CenteredContainer flex={1}>
            <Card shadow="medium" padding="huge" gap="lg">
                {room != null && reports.length > 0 ? (
                    <IssuesList selectedRoom={room} issues={reports}>
                    </IssuesList>
                    ):(
                      <Subtitle>No room selected</Subtitle>
                )}
            </Card>
        </CenteredContainer>
    );
};