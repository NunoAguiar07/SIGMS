import {HomeScreenType} from "../types/HomeScreenType";
import {Container, GridColumn, GridRow} from "../css_styling/common/NewContainers";
import {Title} from "../css_styling/common/Typography";
import {FooterContainer, ScreenContainer} from "../css_styling/common/Layout";
import {Button, ButtonText} from "../css_styling/common/Buttons";
import CalendarScreen from "./CalendarScreen";
import {NotificationsScreen} from "./NotificationsScreen";
import {Logo} from "../components/Logo";
import {isMobile} from "../../utils/DeviceType";
import {ScrollView} from "react-native";

export const HomeScreen = ({ shouldShowCalendar, onLogout, username, schedule, notifications, clearNotification, onClickProfile, onClickRoom }: HomeScreenType) => {
    if (isMobile) {
        return (
            <ScreenContainer backgroundColor={"transparent"}>
                <ScrollView contentContainerStyle={{ padding: 16, flexGrow: 1}}
                            style={{
                                flex: 1 // Takes all available space
                            }}>
                    {/* Header Section */}
                    <Container style={{ marginBottom: 24}}>
                        <Logo />
                        <Title>A tua sala de aula, mais simples.</Title>
                        <Title>Bem vindo, {username}</Title>
                    </Container>

                    {/* Notifications Section */}
                    <Container style={{ marginBottom: 24 }}>
                        <NotificationsScreen
                            notifications={notifications}
                            clearNotification={clearNotification}
                        />
                    </Container>

                    {/* Logout Button */}
                    <Button variant="primary" onPress={onLogout} style={{ marginTop: 16 }}>
                        <ButtonText>Logout</ButtonText>
                    </Button>
                </ScrollView>
            </ScreenContainer>
        );
    }

    return (
        <Container flex={1} padding="md">
            <GridRow>
                <GridColumn widthPercent={shouldShowCalendar ? 30 : 90}>
                    <Logo/>
                    <Title>A tua sala de aula, mais simples.</Title>
                    <Title>Bem vindo, {username}</Title>
                </GridColumn>

                {shouldShowCalendar && (
                    <GridColumn widthPercent={shouldShowCalendar ? 60 : 0}>
                        <CalendarScreen schedule={schedule} onClickProfile={onClickProfile} onClickRoom={onClickRoom}/>
                    </GridColumn>
                )}

                <GridColumn widthPercent={10}>
                    <NotificationsScreen notifications={notifications} clearNotification={clearNotification}/>
                </GridColumn>
            </GridRow>
            <FooterContainer>
                <Button variant="primary" onPress={onLogout}>
                    <ButtonText>Logout</ButtonText>
                </Button>
            </FooterContainer>
        </Container>
    );
};