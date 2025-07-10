import React, { useState, useRef, useEffect } from "react";
import "./CustomDatePicker.css";

const getDaysInMonth = (year, month) => new Date(year, month + 1, 0).getDate();
const getFirstDayOfMonth = (year, month) => new Date(year, month, 1).getDay();
const formatDate = (year, month, day, hour = 0, minute = 0) => `${year}-${String(month + 1).padStart(2, "0")}-${String(day).padStart(2, "0")}T${String(hour).padStart(2, "0")}:${String(minute).padStart(2, "0")}`;

const CustomDatePicker = ({ label, date, setDate, min, className = "" }) => {
  const [showCalendar, setShowCalendar] = useState(false);
  const [currentMonth, setCurrentMonth] = useState(() => new Date(date || Date.now()).getMonth());
  const [currentYear, setCurrentYear] = useState(() => new Date(date || Date.now()).getFullYear());
  const [selectedDay, setSelectedDay] = useState(() => date ? new Date(date).getDate() : null);
  const [hour, setHour] = useState(() => date ? new Date(date).getHours() : 0);
  const [minute, setMinute] = useState(() => date ? new Date(date).getMinutes() : 0);

  const ref = useRef();
  const minDateTime = min ? new Date(min) : null;

  useEffect(() => {
    const handleClickOutside = (e) => {
      if (ref.current && !ref.current.contains(e.target)) {
        setShowCalendar(false);
      }
    };
    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, []);

  const isSameDayAsMin = (day) => {
    if (!minDateTime) return false;
    const selectedDate = new Date(currentYear, currentMonth, day);
    return selectedDate.toDateString() === minDateTime.toDateString();
  };

  const isTimeTooEarly = (selectedHour, selectedMinute) => {
    if (!minDateTime) return false;
    const selectedMinutes = selectedHour * 60 + selectedMinute;
    const minMinutes = minDateTime.getHours() * 60 + minDateTime.getMinutes();
    return selectedMinutes < minMinutes;
  };

  const handleDateClick = (day) => {
    if (minDateTime) {
      const selected = new Date(currentYear, currentMonth, day, hour, minute);
      if (selected < minDateTime) return;
    }
    const formatted = formatDate(currentYear, currentMonth, day, hour, minute);
    setSelectedDay(day);
    setDate(formatted);
    setShowCalendar(false);
  };

  const handleTimeChange = (type, value) => {
    const numeric = Number(value);
    const newHour = type === "hour" ? numeric : hour;
    const newMinute = type === "minute" ? numeric : minute;

    if (selectedDay !== null) {
      if (isSameDayAsMin(selectedDay) && isTimeTooEarly(newHour, newMinute)) return;
      if (type === "hour") setHour(numeric);
      else setMinute(numeric);
      setDate(formatDate(currentYear, currentMonth, selectedDay, newHour, newMinute));
    } else {
      if (type === "hour") setHour(numeric);
      else setMinute(numeric);
    }
  };

  const renderDays = () => {
    const days = [];
    const daysInMonth = getDaysInMonth(currentYear, currentMonth);
    const startDay = getFirstDayOfMonth(currentYear, currentMonth);

    for (let i = 0; i < startDay; i++) {
      days.push(<div key={`empty-${i}`} className="calendar-day empty"></div>);
    }

    for (let d = 1; d <= daysInMonth; d++) {
      const dateString = formatDate(currentYear, currentMonth, d);
      const isDisabled = minDateTime && new Date(dateString) < new Date(minDateTime.toDateString());
      const isSelected = selectedDay === d;

      days.push(
        <div
          key={d}
          className={`calendar-day${isDisabled ? " disabled" : ""}${isSelected ? " selected" : ""}`}
          onClick={() => !isDisabled && handleDateClick(d)}
        >
          {d}
        </div>
      );
    }
    return days;
  };

  const handlePrevMonth = () => {
    if (currentMonth === 0) {
      setCurrentMonth(11);
      setCurrentYear(currentYear - 1);
    } else {
      setCurrentMonth(currentMonth - 1);
    }
  };

  const handleNextMonth = () => {
    if (currentMonth === 11) {
      setCurrentMonth(0);
      setCurrentYear(currentYear + 1);
    } else {
      setCurrentMonth(currentMonth + 1);
    }
  };

  return (
    <div className={`custom-datepicker ${className}`} ref={ref}>
      <div className="datepicker-floating-group">
        <input
          type="text"
          value={date || ""}
          onClick={() => setShowCalendar(!showCalendar)}
          readOnly
          className="datepicker-input"
          placeholder=" "
        />
        <label className={`datepicker-floating-label ${date ? "filled" : ""}`}>{label}</label>
      </div>
      {showCalendar && (
        <div className="calendar-popup">
          <div className="calendar-header">
            <button onClick={handlePrevMonth}>&lt;</button>
            <span>{currentYear}년 {currentMonth + 1}월</span>
            <button onClick={handleNextMonth}>&gt;</button>
          </div>
          <div className="calendar-grid">
            {["일", "월", "화", "수", "목", "금", "토"].map((d) => (
              <div key={d} className="calendar-day header">{d}</div>
            ))}
            {renderDays()}
          </div>
          <div className="time-picker">
            <label>시간 선택:</label>
            <select value={hour} onChange={(e) => handleTimeChange("hour", e.target.value)}>
              {Array.from({ length: 24 }, (_, i) => (
                <option key={i} value={i}>{String(i).padStart(2, "0")}</option>
              ))}
            </select>
            <span>:</span>
            <select value={minute} onChange={(e) => handleTimeChange("minute", e.target.value)}>
              {Array.from({ length: 60 }, (_, i) => (
                <option key={i} value={i}>{String(i).padStart(2, "0")}</option>
              ))}
            </select>
          </div>
        </div>
      )}
    </div>
  );
};

export default CustomDatePicker;
