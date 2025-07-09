import {ScrollView} from "react-native";
import {CalendarScreenType} from "../types/CalendarScreenType";
import {DayType} from "../types/components/DayType";
import {Card, CenteredContainer, ColumnContainer, Container, RowContainer} from "../css_styling/common/NewContainers";
import {theme} from "../css_styling/common/Theme";
import {BodyText, Subtitle} from "../css_styling/common/Typography";
import { Button } from "../css_styling/common/Buttons";



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


    return (
        <CenteredContainer width={"100%"} flex={1}>
            <Card shadow="medium" gap="md" width={"60%"}>
                <ScrollView contentContainerStyle={{height: '100%', display: 'flex', justifyContent: 'center', alignItems: 'center'}}>
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
    <Container backgroundColor={theme.colors.background.tertiary} padding="md" margin="sm" borderRadius={"large"} width={"100%"} alignItems={"flex-start"}>
        <Subtitle color={theme.colors.text.black} family={"bold"}>{day}</Subtitle>
        {events.length > 0 && (
            <Container backgroundColor={theme.colors.background.cream} padding="md" margin="sm" borderRadius={"large"} width={"100%"} gap={"md"}>
                {events.map((event: any) => (
                    <RowContainer borderRadius={"small"} justifyContent={"space-between"}>
                        <ColumnContainer gap={"sm"}>
                            <BodyText family={"bold"}>{event.time}</BodyText>
                            <BodyText>{event.title}</BodyText>
                        </ColumnContainer>
                        <RowContainer gap="md" >
                            {event.teachers.map((teacher: any, i: number) => (
                                <Button
                                    key={i}
                                    variant={"white"}
                                    onPress={() => onClickProfile(teacher.id)}
                                >
                                    <BodyText family={"bold"}>‍🏫 {teacher.name}</BodyText>
                                </Button>
                            ))}
                            <Button
                                onPress={() => onClickRoom(event.roomId)}
                                variant={"white"}
                            >
                                <BodyText family={"bold"}>📄 Room Reports</BodyText>
                            </Button>
                        </RowContainer>
                    </RowContainer>
                ))}
            </Container>
        )}
    </Container>
);

export default CalendarScreen;