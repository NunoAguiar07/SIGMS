import {ScrollView} from "react-native";
import {CalendarScreenType} from "../types/CalendarScreenType";
import {DayType} from "../types/components/DayType";
import {Card, CenteredContainer, ColumnContainer, Container, RowContainer} from "../css_styling/common/NewContainers";
import {theme} from "../css_styling/common/Theme";
import {BodyText, Subtitle} from "../css_styling/common/Typography";
import {ActionButton, Button} from "../css_styling/common/Buttons";
import {isMobile} from "../../utils/DeviceType";
import {useState} from "react";
import {Ionicons} from "@expo/vector-icons";



const CalendarScreen = ({ schedule, onClickProfile, onClickRoom }: CalendarScreenType) => {

    const getEventsForDay = (dayName: string) => {
        // @ts-ignore
        return schedule.filter(item => item.lecture.weekDay === dayName)
            .map(item => ({
                time: `${item.lecture.startTime} ➜ ${item.lecture.endTime}`,
                title: `${item.lecture.schoolClass.subject.name} (${item.lecture.type}), ${item.lecture.schoolClass.name}: ${item.lecture.room.name}`,
                roomId: item.lecture.room.id, // add this
                teachers: item.teacher.map(t => ({ name: t.user.username, id: t.user.id })),
            }));
    };

    const getCurrentDay = (): string => {
        const days = ['SUNDAY', 'MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY'];
        return days[new Date().getDay()];
    };

    const [selectedDay, setSelectedDay] = useState<string>(getCurrentDay());
    const daysOrder = ['MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY'];

    const navigateDay = (direction: 'prev' | 'next') => {
        const currentIndex = daysOrder.indexOf(selectedDay);
        if (direction === 'prev' && currentIndex > 0) {
            setSelectedDay(daysOrder[currentIndex - 1]);
        } else if (direction === 'next' && currentIndex < daysOrder.length - 1) {
            setSelectedDay(daysOrder[currentIndex + 1]);
        }
    };


    if (isMobile) {
        return (
            <CenteredContainer flex={1}>
                <Card shadow="medium" gap="md" width={"90%"} height={"75%"} justifyContent={"space-between"} >
                    <CenteredContainer>
                        <ScrollView horizontal showsHorizontalScrollIndicator={false} contentContainerStyle={{padding: 10}} >
                            <RowContainer gap="sm" height={"100%"} alignItems="center">
                                {daysOrder.map(day => (
                                    <Button
                                        key={day}
                                        onPress={() => setSelectedDay(day)}
                                        variant={selectedDay === day ? "warning" : "white"}
                                        size="small"
                                    >
                                        <BodyText
                                            family={"bold"}
                                        >{day.slice(0, 3)}</BodyText>
                                    </Button>
                                ))}
                            </RowContainer>
                        </ScrollView>
                    </CenteredContainer>
                    {getEventsForDay(selectedDay).length === 0 ? (
                        <Subtitle>No events for today</Subtitle>
                    ): (
                        <DaySection
                            day={selectedDay}
                            events={getEventsForDay(selectedDay)}
                            onClickProfile={onClickProfile}
                            onClickRoom={onClickRoom}
                        />
                    )}
                    <RowContainer justifyContent="space-between" alignItems="center" padding="md">
                        <ActionButton
                            onPress={() => navigateDay('prev')}
                            disabled={daysOrder.indexOf(selectedDay) === 0}
                        >
                            <Ionicons name={"arrow-back-circle-sharp"} color={"white"}></Ionicons>
                        </ActionButton>


                        <Subtitle family="bold">
                            {selectedDay.charAt(0) + selectedDay.slice(1).toLowerCase()}
                            {selectedDay === getCurrentDay() && " (Today)"}
                        </Subtitle>

                        <ActionButton
                            onPress={() => navigateDay('next')}
                            disabled={daysOrder.indexOf(selectedDay) === daysOrder.length - 1}
                        >
                            <Ionicons name={"arrow-forward-circle-sharp"} color={"white"}></Ionicons>
                        </ActionButton>
                    </RowContainer>
                </Card>
            </CenteredContainer>
        );
    }

    return (
        <CenteredContainer width={"100%"} flex={1}>
            <Card shadow="medium" gap="md" width={"60%"} flex={1} height={"90%"}>
                <ScrollView>
                    <DaySection day="Monday"  events={getEventsForDay("MONDAY")} onClickProfile={onClickProfile} onClickRoom={onClickRoom} />
                    <DaySection day="Tuesday"  events={getEventsForDay("TUESDAY")} onClickProfile={onClickProfile} onClickRoom={onClickRoom} />
                    <DaySection day="Wednesday"  events={getEventsForDay("WEDNESDAY")} onClickProfile={onClickProfile} onClickRoom={onClickRoom} />
                    <DaySection day="Thursday"  events={getEventsForDay("THURSDAY")} onClickProfile={onClickProfile} onClickRoom={onClickRoom} />
                    <DaySection day="Friday"  events={getEventsForDay("FRIDAY")} onClickProfile={onClickProfile} onClickRoom={onClickRoom} />
                    <DaySection day="Saturday"  events={getEventsForDay("SATURDAY")} onClickProfile={onClickProfile} onClickRoom={onClickRoom} />
                </ScrollView>
            </Card>
        </CenteredContainer>
    );
};

const DaySection = ({ day, events, onClickProfile, onClickRoom } : DayType) => (
    <Container backgroundColor={theme.colors.background.tertiary} padding="md" margin="sm" borderRadius={"large"} width={"98%"} alignItems={isMobile ? "center" : "flex-start"}>
        {!isMobile && (
            <Subtitle color={theme.colors.text.black} family={"bold"}>
                {day}
            </Subtitle>
        )}
        {events.length > 0 && (
            <Container backgroundColor={theme.colors.background.cream} padding="md" margin="sm" borderRadius={"large"} width={"98%"} gap={"md"}>
                {events.map((event: any) => (
                    <ColumnContainer borderRadius={"small"} gap="sm"> {/* Changed to ColumnContainer */}
                        <RowContainer justifyContent={"space-between"}>
                            <ColumnContainer gap={"sm"}>
                                <BodyText family={"bold"}>{event.time}</BodyText>
                                <BodyText numberOfLines={isMobile ? 2 : undefined}>{event.title} </BodyText>
                            </ColumnContainer>
                        </RowContainer>

                        <Container gap="md" flexDirection={isMobile ? "column" : "row"} alignItems={isMobile ? "center" : "flex-start"}>
                            {event.teachers.map((teacher: any, i: number) => (
                                <Button
                                    key={i}
                                    variant={"white"}
                                    size={"small"}
                                    onPress={() => onClickProfile(teacher.id)}
                                >
                                    <BodyText family={"bold"}>‍🏫 {teacher.name}</BodyText>
                                </Button>
                            ))}
                            <Button
                                onPress={() => onClickRoom(event.roomId)}
                                variant={"white"}
                                size={"small"}
                            >
                                <BodyText family={"bold"}>📄 Room Reports</BodyText>
                            </Button>
                        </Container>
                    </ColumnContainer>
                ))}
            </Container>
        )}
    </Container>
);

export default CalendarScreen;