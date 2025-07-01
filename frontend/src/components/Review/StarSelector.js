// src/components/Review/StarSelector.jsx
import React from 'react';

function StarSelector({ rating, onChange, max = 5, size = 28 }) {
  return (
    <div className="flex gap-1">
      {[...Array(max)].map((_, idx) => {
        const value = idx + 1;
        return (
          <span
            key={value}
            onClick={() => onChange(value)}
            style={{ fontSize: size, cursor: 'pointer', color: value <= rating ? '#facc15' : '#e5e7eb' }}
          >
            ★
          </span>
        );
      })}
    </div>
  );
}

export default StarSelector;
