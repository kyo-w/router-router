export function delay(func, times){
  return  new Promise((resolve, reject) => {
    setTimeout(()=>resolve(func()), times)
  })
}
