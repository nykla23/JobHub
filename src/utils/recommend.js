export function shuffleArray(list = []) {
  const arr = [...list];
  for (let i = arr.length - 1; i > 0; i -= 1) {
    const j = Math.floor(Math.random() * (i + 1));
    [arr[i], arr[j]] = [arr[j], arr[i]];
  }
  return arr;
}

export function pickPage(list = [], page = 0, pageSize = 4) {
  if (!list.length) return [];
  const shuffled = shuffleArray(list);
  const start = (page * pageSize) % shuffled.length;
  return shuffled.slice(start, start + pageSize);
}
