import {RoomReportsScreenType} from "../types/RoomReportScreenType";
import {IssuesList} from "./CreateReportScreen";
import {Card, CenteredContainer} from "../css_styling/common/NewContainers";
import {Subtitle} from "../css_styling/common/Typography";


export const RoomReportsScreen = ({ reports, room }: RoomReportsScreenType) => {
    return (
        <CenteredContainer flex={1}>
            {room != null ? (
                <IssuesList selectedRoom={room} issues={reports}>
                </IssuesList>
                ):(
                <Card shadow="medium" padding="huge" gap="lg">
                  <Subtitle>No reports for this room</Subtitle>
                </Card>
            )}
        </CenteredContainer>
    );
};