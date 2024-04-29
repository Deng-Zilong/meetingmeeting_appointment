import service from './axios-config'

const currying = () => {
  return {

    POST: (url: string, data: {} = {}, headers?: any) => {
      return service.request({
        url,
        method: 'POST',
        data,
        headers
      })
    },

    GET: (url: string, params: {} = {}, headers?: any) => {
      return service.request({
        url,
        method: 'GET',
        params,
        headers
      })
    },
    PUT: (url: string, data: {} = {}, headers?: any) => {
      return service.request({
        url,
        method: 'PUT',
        data,
        headers
      })
    },
    DELETE: (url: string, headers?: any) => {
      return service.request({
        url,
        method: 'DELETE',
        headers
      })
    }

  }
}

export default currying();
