import service from './axios-config'

const currying = () => {
  return {

    post: (url: string, data: {} = {}, responseType?: any, headers?: any) => {
      return service.request({
        url,
        method: 'POST',
        data,
        headers,
        responseType
      })
    },

    get: (url: string, params: {} = {}, responseType?: any, headers?: any) => {
      return service.request({
        url,
        method: 'GET',
        params,
        headers,
        responseType
      })
    },
    put: (url: string, data: {} = {}, headers?: any) => {
      return service.request({
        url,
        method: 'PUT',
        data,
        headers
      })
    },
    delete: (url: string, params: {} = {}, headers?: any) => {
      return service.request({
        url,
        method: 'DELETE',
        params,
        headers
      })
    }

  }
}

export default currying();
