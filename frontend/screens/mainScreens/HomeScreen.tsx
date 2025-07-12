import {HomeScreenType} from "../types/HomeScreenType";
import {Container, GridColumn, GridRow} from "../css_styling/common/NewContainers";
import {Title} from "../css_styling/common/Typography";
import {FooterContainer, ScreenContainer} from "../css_styling/common/Layout";
import {Button, ButtonText} from "../css_styling/common/Buttons";
import CalendarScreen from "./CalendarScreen";
import {NotificationsScreen} from "./NotificationsScreen";
import {Logo} from "../components/Logo";
import {isMobile} from "../../utils/DeviceType";

export const HomeScreen = ({ shouldShowCalendar, onLogout, username, schedule, notifications, clearNotification, onClickProfile, onClickRoom }: HomeScreenType) => {
    if (isMobile) {
        return (
            <ScreenContainer backgroundColor={"transparent"}>
                <Container style={{ marginBottom: 24}}>
                    <Logo />
                    <Title>A tua sala de aula, mais simples.</Title>
                    <Title>Bem vindo, {username}</Title>
                </Container>
                <Button variant="primary" onPress={onLogout} style={{ marginTop: 16 }}>
                    <ButtonText>Logout</ButtonText>
                </Button>
            </ScreenContainer>
        );
    }

    return (
        <Container flex={1} padding="md">
            <GridRow flex={1}>
                <GridColumn widthPercent={shouldShowCalendar ? 15 : 70}>
                    <Logo/>
                    <Title>A tua sala de aula, mais simples.</Title>
                    <Title>Bem vindo, {username}</Title>
                </GridColumn>

                {shouldShowCalendar && (
                    <GridColumn widthPercent={shouldShowCalendar ? 65 : 0}>
                        <CalendarScreen schedule={schedule} onClickProfile={onClickProfile} onClickRoom={onClickRoom}/>
                    </GridColumn>
                )}

                <GridColumn widthPercent={shouldShowCalendar ? 20 : 30}>
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