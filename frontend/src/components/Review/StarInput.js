import React, { useState } from 'react';

function StarInput({ value = 0, onChange, max = 5, size = 24 }) {
  const [hovered, setHovered] = useState(0);

  const handleClick = (index) => {
    onChange(index + 1);
  };

  return (
    <div className="flex gap-1.5">
      {[...Array(max)].map((_, i) => {
        const isFilled = hovered ? i < hovered : i < Number(value); 
        return (
          <span
            key={i}
            onClick={() => handleClick(i)}
            onMouseEnter={() => setHovered(i + 1)}
            onMouseLeave={() => setHovered(0)}
            className={`cursor-pointer transition transform duration-150 
              ${isFilled ? 'text-yellow-400' : 'text-gray-300'} 
              hover:scale-110`}
            style={{ fontSize: size }}
          >
            ★
          </span>
        );
      })}
    </div>
  );
}


export default StarInput;
