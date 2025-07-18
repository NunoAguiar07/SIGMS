

export interface CalendarScreenType {
    onClickProfile : (id:number) => void;
    onClickRoom : (id:number) => void;
    getEventsForDay: (dayName: string) => any[];
    getCurrentDay: () => string;
    selectedDay: string;
    setSelectedDay: (day: string) => void;
    daysOrder: string[];
    navigateDay: (direction: 'prev' | 'next') => void;
}