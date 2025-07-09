import React from 'react';

function StarRating({ rating = 0, max = 5, size = 18 }) {
  const fullStars = Math.floor(rating);
  const emptyStars = max - fullStars;

  return (
    <div className="flex gap-0.5 items-center">
      {[...Array(fullStars)].map((_, i) => (
        <span key={`full-${i}`} style={{ fontSize: size, color: '#7c3aed' }}>★</span>
      ))}
      {[...Array(emptyStars)].map((_, i) => (
        <span key={`empty-${i}`} style={{ fontSize: size, color: '#9ca3af' }}>☆</span>
      ))}
    </div>
  );
}

export default StarRating;
