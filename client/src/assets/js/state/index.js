export function createState(initialState, onChange) {
  return new Proxy(initialState, {
    set(target, key, value) {
      target[key] = value
      onChange(key, value)
      return true
    },
  })
}
