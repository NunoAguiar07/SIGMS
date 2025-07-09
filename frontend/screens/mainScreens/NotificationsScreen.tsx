import {FlatList} from "react-native";
import {NotificationComponent} from "../components/notifications/Notification";
import {Notification} from "../../types/notifications/Notification";
import {Card, CenteredContainer} from "../css_styling/common/NewContainers";
import {Title} from "../css_styling/common/Typography";
import {FlatListContainer} from "../css_styling/common/FlatList";

export const NotificationsScreen = ({notifications, clearNotification}: {notifications: Notification[], clearNotification: (notification: Notification) => void }) => {
    return (
        <CenteredContainer justifyContent="center" padding="md">
            <Card shadow="medium" alignItems={"center"} gap="md">
                <Title>Notifications</Title>
                <FlatListContainer position={"static"}>
                    <FlatList
                        data={notifications}
                        keyExtractor={(item) => item.id?.toString() || `${item.title}-${item.message}`}
                        renderItem={({ item }) => (
                            <NotificationComponent
                                notification={item}
                                clear={clearNotification}
                            />
                        )}
                    />
                </FlatListContainer>
            </Card>
        </CenteredContainer>
        // <View style={[ContainerStyles.columnBase,ContainerStyles.backgroundSecondary,ContainerStyles.roundedContainer, ContainerStyles.border, ContainerStyles.borderPrimary, ContainerStyles.fullHeight]}>
        //     <View style={[ContainerStyles.roundedContainer,{backgroundColor: '#d7c1ba'}]}>
        //         <Text style={[TextStyles.black, TextStyles.h1]}>Notifications</Text>
        //     </View>
        //     <View style={getColumnStyle(100,10)}>
        //         {notifications.map((notification) => (
        //             <NotificationComponent notification={notification} clear={clearNotification}/>
        //         ))}
        //     </View>
        // </View>
    );
}