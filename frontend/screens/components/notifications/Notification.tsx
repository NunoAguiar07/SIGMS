import {Notification} from "../../../types/notifications/Notification";
import {Ionicons} from "@expo/vector-icons";
import {FlatListItem} from "../../css_styling/common/FlatList";
import {BodyText, Subtitle} from "../../css_styling/common/Typography";
import {theme} from "../../css_styling/common/Theme";
import {Button} from "../../css_styling/common/Buttons";

export const NotificationComponent = ({notification, clear}: {notification: Notification, clear: (notification: Notification) => void}) => {
    return (
        <FlatListItem>
            <Subtitle color={theme.colors.text.black}>{notification.title}</Subtitle>
            <BodyText>{notification.message}</BodyText>
            <Button
                onPress={() => {
                    console.log(`Clearing ${notification.id}`);
                    clear(notification);
                }}
                variant="primary"
            >
                <Ionicons name={'arrow-redo-outline'} size={30} color="white" />
            </Button>
        </FlatListItem>
    )
}